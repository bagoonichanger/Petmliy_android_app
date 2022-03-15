package com.bagooni.petmliy_android_app.map.model.metadata

import com.bagooni.petmliy_android_app.map.model.RegionInfo.RegionInfo

data class Meta(
    var total_count: Int,               // 검색어에 검색된 문서 수
    var same_name: RegionInfo          // 질의어의 지역 및 키워드 분석 정보
)