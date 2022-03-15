package com.bagooni.petmliy_android_app.map.model.RegionInfo

data class RegionInfo(
    var region: List<String>,           // 질의어에서 인식된 지역의 리스트, ex) '중앙로 맛집' 에서 중앙로에 해당하는 지역 리스트
    var keyword: String,                // 질의어에서 지역 정보를 제외한 키워드, ex) '중앙로 맛집' 에서 '맛집'
    var selected_region: String         // 인식된 지역 리스트 중, 현재 검색에 사용된 지역 정보
)
