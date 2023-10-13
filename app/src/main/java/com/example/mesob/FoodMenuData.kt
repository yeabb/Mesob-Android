package com.example.mesob

import com.google.firebase.firestore.GeoPoint

data class FoodMenuData(
    var foodName: String = "",
    var restaurantName: String = "",
    var restaurantAdress: String = "",
    var restaurantPhone: String = "",
    var location : GeoPoint? = null,
    var foodCreditNumber : Int = 0,
    var foodIngredients : String = "",
    var rating : Int = 0,
    var numberOfReviews : Int = 0,
    var totalSumRating : Int = 0,
)
