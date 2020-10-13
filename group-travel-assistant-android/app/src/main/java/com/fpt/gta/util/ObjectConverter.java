package com.fpt.gta.util;

import android.content.Context;
import android.content.SharedPreferences;

import androidx.room.TypeConverter;

import com.fpt.gta.data.dto.MemberDTO;
import com.fpt.gta.data.dto.PlanDTO;

import com.fpt.gta.webService.gson.GsonUtil;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.List;

public class ObjectConverter {
    public static  String fromPlanDTOList(MemberDTO.PersonDTO personDTO) {
        if (personDTO == null) {
            return (null);
        }
        Gson gson = GsonUtil.getGson();
        Type type = new TypeToken<PlanDTO> () {
        }.getType();
        String json = gson.toJson(personDTO, type);
        return json;
    }

    @TypeConverter // note this annotation
    public static PlanDTO toExtraList(String planDtoString) {
        if (planDtoString == null) {
            return (null);
        }
        Gson gson = GsonUtil.getGson();
        Type type = new TypeToken<PlanDTO>() {
        }.getType();
        PlanDTO cardType = gson.fromJson(planDtoString, type);
        return cardType;
    }


    public static  String fromMemberDTOList(MemberDTO memberDTO) {
        if (memberDTO == null) {
            return (null);
        }
        Gson gson = GsonUtil.getGson();
        Type type = new TypeToken<MemberDTO> () {
        }.getType();
        String json = gson.toJson(memberDTO, type);
        return json;
    }

    @TypeConverter // note this annotation
    public static List<MemberDTO> toMemberExtraList(String memberDtoString) {
        if (memberDtoString == null) {
            return (null);
        }
        Gson gson = GsonUtil.getGson();
        Type type = new TypeToken <List<MemberDTO>>() {
        }.getType();
        List<MemberDTO> memberDTO = gson.fromJson(memberDtoString, type);
        return memberDTO;
    }






}
