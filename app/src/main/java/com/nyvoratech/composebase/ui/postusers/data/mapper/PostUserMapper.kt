package com.nyvoratech.composebase.ui.postusers.data.mapper

import com.nyvoratech.composebase.ui.postusers.data.modeldto.PostUserDto
import com.nyvoratech.composebase.domain.model.Address
import com.nyvoratech.composebase.domain.model.Company
import com.nyvoratech.composebase.domain.model.Geo
import com.nyvoratech.composebase.domain.model.PostUser

fun List<PostUserDto>.toDomain(): List<PostUser> {
    return map {
        it.toPostUser()
    }
}

fun PostUserDto.toPostUser(): PostUser {
    return PostUser(
        id = id,
        name = name,
        username = username,
        email = email,
        address = Address(
            street = address.street,
            suite = address.suite,
            city = address.city,
            zipcode = address.zipcode,
            geo = Geo(
                lat = address.geo.lat,
                lng = address.geo.lng
            )
        ),
        phone = phone,
        website = website,
        company = Company(
            name = company.name,
            catchPhrase = company.catchPhrase,
            bs = company.bs
        )
    )
}