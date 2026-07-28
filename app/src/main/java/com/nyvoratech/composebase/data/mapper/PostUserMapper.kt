package com.nyvoratech.composebase.data.mapper

import com.nyvoratech.composebase.data.remote.dto.PostUserDto
import com.nyvoratech.composebase.domain.model.Address
import com.nyvoratech.composebase.domain.model.Company
import com.nyvoratech.composebase.domain.model.Geo
import com.nyvoratech.composebase.domain.model.PostUser
import com.nyvoratech.composebase.domain.model.PostUserResponse

fun PostUserDto.toDomain(): PostUser = PostUser(
    id = id,
    name = name,
    username = username,
    email = email,
    address = Address(
        street = address.street,
        suite = address.suite,
        city = address.city,
        zipcode = address.zipcode,
        geo = Geo(lat = address.geo.lat, lng = address.geo.lng)
    ),
    phone = phone,
    website = website,
    company = Company(
        name = company.name,
        catchPhrase = company.catchPhrase,
        bs = company.bs
    )
)

fun List<PostUserDto>.toPostUserResponse(): PostUserResponse =
    PostUserResponse(postUserList = map { it.toDomain() })