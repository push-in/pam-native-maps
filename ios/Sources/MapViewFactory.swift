import CoreLocation
import Foundation
import MapKit
import PamNative
import UIKit

public final class MapViewFactory: NativeViewFactory, @unchecked Sendable {
    public init() {}
    public func create(context:AnyObject?,emit:@escaping(Data)->Void)->UIView{PamMapView(emit:emit)}
    public func update(view:UIView,properties:[String:WireValue]){(view as? PamMapView)?.update(properties)}
    public func release(view:UIView){(view as? PamMapView)?.releaseMap()}
}
private final class PamMapView:MKMapView,MKMapViewDelegate,@unchecked Sendable {
    private let event:(Data)->Void;private var markerJSON="";private var applying=false
    init(emit:@escaping(Data)->Void){event=emit;super.init(frame:.zero);delegate=self;send(["event":.integer(1)])}
    required init?(coder:NSCoder){nil}
    func update(_ v:[String:WireValue]){applying=true;let center=CLLocationCoordinate2D(latitude:v.decimal("latitude",0),longitude:v.decimal("longitude",0));let zoom=max(1,min(22,v.decimal("zoom",2)));let span=360/pow(2,zoom);setRegion(MKCoordinateRegion(center:center,span:MKCoordinateSpan(latitudeDelta:span,longitudeDelta:span)),animated:false);mapType=whenStyle(v.integer("style",1));isZoomEnabled=v.flag("gestures",true);isScrollEnabled=isZoomEnabled;showsUserLocation=v.flag("myLocation",false);let json=v.text("markers");if json != markerJSON{markerJSON=json;replaceMarkers(json)};applying=false}
    private func replaceMarkers(_ json:String){removeAnnotations(annotations.filter{!($0 is MKUserLocation)});guard let data=json.data(using:.utf8),let rows=try? JSONSerialization.jsonObject(with:data)as?[[String:Any]]else{failure("Invalid marker JSON");return};for row in rows{guard let id=row["id"]as?String,let lat=row["latitude"]as?Double,let lng=row["longitude"]as?Double else{continue};let point=PamAnnotation(identifier:id);point.coordinate=.init(latitude:lat,longitude:lng);point.title=row["title"]as?String;point.subtitle=row["subtitle"]as?String;addAnnotation(point)}}
    func mapView(_ mapView:MKMapView,regionDidChangeAnimated animated:Bool){guard !applying else{return};send(["event":.integer(2),"latitude":.decimal(centerCoordinate.latitude),"longitude":.decimal(centerCoordinate.longitude)])}
    func mapView(_ mapView:MKMapView,didSelect view:MKAnnotationView){if let marker=view.annotation as?PamAnnotation{send(["event":.integer(3),"markerId":.text(marker.identifier)])}}
    override func touchesEnded(_ touches:Set<UITouch>,with event:UIEvent?){if let touch=touches.first{let p=convert(touch.location(in:self),toCoordinateFrom:self);send(["event":.integer(4),"latitude":.decimal(p.latitude),"longitude":.decimal(p.longitude)])};super.touchesEnded(touches,with:event)}
    private func whenStyle(_ value:Int64)->MKMapType{switch value{case 2:return.satellite;case 3:return.hybrid;default:return.standard}}
    private func failure(_ message:String){send(["event":.integer(5),"message":.text(message)])};private func send(_ values:[String:WireValue]){if let data=try?WireMap.encode(values){event(data)}};func releaseMap(){delegate=nil;removeAnnotations(annotations)}
}
private final class PamAnnotation:MKPointAnnotation{let identifier:String;init(identifier:String){self.identifier=identifier;super.init()}}
private extension Dictionary where Key==String,Value==WireValue{func text(_ k:String)->String{if case let.text(v)?=self[k]{return v};return""};func flag(_ k:String,_ f:Bool)->Bool{if case let.flag(v)?=self[k]{return v};return f};func integer(_ k:String,_ f:Int64)->Int64{if case let.integer(v)?=self[k]{return v};return f};func decimal(_ k:String,_ f:Double)->Double{switch self[k]{case let.decimal(v)?:return v;case let.integer(v)?:return Double(v);default:return f}}}
