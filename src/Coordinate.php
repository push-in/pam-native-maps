<?php
declare(strict_types=1);namespace Pam\Native\Maps;use InvalidArgumentException;use JsonSerializable;
final readonly class Coordinate implements JsonSerializable{public function __construct(public float$latitude,public float$longitude){if(!is_finite($latitude)||!is_finite($longitude)||$latitude < -90||$latitude > 90||$longitude < -180||$longitude > 180)throw new InvalidArgumentException('Coordinates are outside valid latitude/longitude bounds.');}public function jsonSerialize():array{return['latitude'=>$this->latitude,'longitude'=>$this->longitude];}}
