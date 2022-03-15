package com.bagooni.petmliy_android_app.map.model.documents

data class PlaceModel(
    var id: String,                     // 장소 ID
    var place_name: String,             // 장소명, 업체명
    var category_name: String,          // 카테고리 이름
    var phone: String,                  // 전화번호
    var address_name: String,           // 전체 지번 주소
    var road_address_name: String,      // 전체 도로명 주소
    var x: String,                      // X 좌표값 혹은 longitude
    var y: String,                      // Y 좌표값 혹은 latitude
    var place_url: String,              // 장소 상세페이지 URL
    var distance: String                 // 중심좌표까지의 거리. 단, x,y 파라미터를 준 경우에만 존재. 단위는 meter
)