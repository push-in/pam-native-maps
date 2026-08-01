<?php
declare(strict_types=1);namespace Pam\Native\Maps;use Closure;use Pam\Native\Element;use Pam\Native\Internal\Wire;use Pam\Native\Renderable;use Pam\Native\UI\CustomView;
final class MapView implements Renderable
{
 private array$properties=['latitude'=>0.0,'longitude'=>0.0,'zoom'=>2.0,'style'=>1,'markers'=>'[]','myLocation'=>false,'gestures'=>true];private ?Closure$handler=null;
 public static function make(Coordinate$center=new Coordinate(0,0),float$zoom=2):self{$view=new self();return$view->camera($center,$zoom);}
 public function camera(Coordinate$center,float$zoom):self{return$this->with(['latitude'=>$center->latitude,'longitude'=>$center->longitude,'zoom'=>max(1,min(22,$zoom))]);}
 public function style(MapStyle$style):self{return$this->with(['style'=>$style->value]);}
 /** @param list<MapMarker>$markers */public function markers(array$markers):self{foreach($markers as$marker)if(!$marker instanceof MapMarker)throw new \InvalidArgumentException('Markers must be MapMarker values.');if(count($markers)>5000)throw new \InvalidArgumentException('A map supports at most 5,000 markers.');return$this->with(['markers'=>json_encode($markers,JSON_THROW_ON_ERROR|JSON_UNESCAPED_SLASHES|JSON_UNESCAPED_UNICODE)]);}
 public function showUserLocation(bool$value=true):self{return$this->with(['myLocation'=>$value]);}public function gestures(bool$value=true):self{return$this->with(['gestures'=>$value]);}
 /** @param Closure(MapEventKind,array<string,mixed>):void$handler */public function onEvent(Closure$handler):self{$copy=clone$this;$copy->handler=$handler;return$copy;}
 public function toElement():Element{$view=CustomView::make('maps.map',$this->properties);return$this->handler===null?$view:$view->onNativeEvent(function(string$payload):void{$values=Wire::decodeMap($payload);($this->handler)(MapEventKind::tryFrom((int)($values['event']??5))??MapEventKind::Error,$values);});}
 private function with(array$values):self{$copy=clone$this;$copy->properties=[...$copy->properties,...$values];return$copy;}
}
