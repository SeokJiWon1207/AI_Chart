package com.kfitchart.entity

sealed interface KfitConnectionStateEntity {

    /**
     * 연결중
     */
    object OnConnectionOpening : KfitConnectionStateEntity

    /**
     * 연결됨
     */
    object OnConnectionOpened : KfitConnectionStateEntity

    /**
     * 연결중 연결 실패
     * (재시도 10번 후 실패한 경우)
     */
    object OnConnectionFail : KfitConnectionStateEntity
}
