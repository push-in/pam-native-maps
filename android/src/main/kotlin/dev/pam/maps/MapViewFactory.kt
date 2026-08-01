package dev.pam.maps

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.View
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.MapView
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import dev.pam.nativeapp.protocol.WireMap
import dev.pam.nativeapp.protocol.WireValue
import dev.pam.nativeapp.views.NativeViewFactory
import org.json.JSONArray

class MapViewFactory(private val applicationContext: Context) : NativeViewFactory {
    override fun create(context: Context, emit: (ByteArray) -> Unit): View = PamMapView(context, emit)
    override fun update(view: View, properties: Map<String, WireValue>) = (view as PamMapView).update(properties)
    override fun release(view: View) = (view as PamMapView).releaseMap()

    private class PamMapView(context: Context, private val emit: (ByteArray) -> Unit) : MapView(context), OnMapReadyCallback {
        private var map: GoogleMap? = null
        private var pending: Map<String, WireValue> = emptyMap()
        private var markerJson = ""
        init { onCreate(Bundle()); getMapAsync(this); onStart(); onResume() }
        override fun onMapReady(googleMap: GoogleMap) {
            map = googleMap
            googleMap.setOnCameraIdleListener { val p=googleMap.cameraPosition; send(mapOf("event" to WireValue.Integer(2),"latitude" to WireValue.Decimal(p.target.latitude),"longitude" to WireValue.Decimal(p.target.longitude),"zoom" to WireValue.Decimal(p.zoom.toDouble()))) }
            googleMap.setOnMapClickListener { send(mapOf("event" to WireValue.Integer(4),"latitude" to WireValue.Decimal(it.latitude),"longitude" to WireValue.Decimal(it.longitude))) }
            googleMap.setOnMarkerClickListener { marker -> send(mapOf("event" to WireValue.Integer(3),"markerId" to WireValue.Text(marker.tag as? String ?: ""))); false }
            apply(pending); send(mapOf("event" to WireValue.Integer(1)))
        }
        fun update(values: Map<String, WireValue>) { pending=values; if(map!=null)apply(values) }
        private fun apply(values: Map<String, WireValue>) {
            val google=map?:return; val center=LatLng(values.decimal("latitude",0.0),values.decimal("longitude",0.0)); google.moveCamera(CameraUpdateFactory.newLatLngZoom(center,values.decimal("zoom",2.0).toFloat()))
            google.mapType=when(values.integer("style",1)){2L->GoogleMap.MAP_TYPE_SATELLITE;3L->GoogleMap.MAP_TYPE_HYBRID;4L->GoogleMap.MAP_TYPE_TERRAIN;else->GoogleMap.MAP_TYPE_NORMAL}
            val gestures=values.flag("gestures",true);google.uiSettings.setAllGesturesEnabled(gestures)
            val requestedLocation=values.flag("myLocation",false);val allowed=context.checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION)==PackageManager.PERMISSION_GRANTED;google.isMyLocationEnabled=requestedLocation&&allowed
            val json=values.text("markers");if(json!=markerJson){markerJson=json;google.clear();try{val list=JSONArray(json);for(i in 0 until list.length()){val item=list.getJSONObject(i);val marker=google.addMarker(MarkerOptions().position(LatLng(item.getDouble("latitude"),item.getDouble("longitude"))).title(item.optString("title")).snippet(item.optString("subtitle")));marker?.tag=item.getString("id")}}catch(error:Exception){send(mapOf("event" to WireValue.Integer(5),"message" to WireValue.Text(error.message.orEmpty())))}}
        }
        fun releaseMap(){onPause();onStop();onDestroy();map=null}
        private fun send(values:Map<String,WireValue>)=emit(WireMap.encode(values))
        private fun Map<String,WireValue>.text(key:String)=(get(key)as?WireValue.Text)?.value.orEmpty()
        private fun Map<String,WireValue>.flag(key:String,fallback:Boolean)=(get(key)as?WireValue.Flag)?.value?:fallback
        private fun Map<String,WireValue>.integer(key:String,fallback:Long)=(get(key)as?WireValue.Integer)?.value?:fallback
        private fun Map<String,WireValue>.decimal(key:String,fallback:Double)=when(val v=get(key)){is WireValue.Decimal->v.value;is WireValue.Integer->v.value.toDouble();else->fallback}
    }
}
