package com.bagooni.petmliy_android_app.map.model.Response

import com.bagooni.petmliy_android_app.map.model.documents.PlaceModel
import com.bagooni.petmliy_android_app.map.model.metadata.Meta

data class PlaceDto(
    var meta: Meta?,                // 장소 메타데이터
    var documents: List<PlaceModel>?          // 검색 결과
)
