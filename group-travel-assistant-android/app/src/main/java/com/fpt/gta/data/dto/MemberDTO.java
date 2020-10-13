package com.fpt.gta.data.dto;

import androidx.room.TypeConverter;
import androidx.room.TypeConverters;

import com.fpt.gta.data.dto.constant.MemberRole;
import com.fpt.gta.util.ObjectConverter;
import com.fpt.gta.webService.gson.Exclude;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.math.BigDecimal;

import lombok.Data;

@Data
public class MemberDTO implements Serializable {

    @SerializedName("id")
    private int id = 0;
    @SerializedName("expectedPrice")
    private BigDecimal expectedPrice;
    @SerializedName("idRole")
    private Integer idRole= MemberRole.MEMBER;
    @SerializedName("idStatus")
    private Integer idStatus;
    @SerializedName("isReady")
    private Boolean isReady;
    @Exclude
    private boolean isSelected;
    @SerializedName("activityBudget")
    private BigDecimal activityBudget = BigDecimal.ZERO;
    @SerializedName("accommodationBudget")
    private BigDecimal accommodationBudget = BigDecimal.ZERO;
    @SerializedName("transportationBudget")
    private BigDecimal transportationBudget = BigDecimal.ZERO;
    @SerializedName("foodBudget")
    private BigDecimal foodBudget = BigDecimal.ZERO;

    @TypeConverters(ObjectConverter.class)
    @SerializedName("person")
    private PersonDTO person = new PersonDTO();


    @Data
    public static class PersonDTO implements Serializable {
        @SerializedName("id")
        private Integer id = 0;
        @SerializedName("name")
        private String name = "";
        @SerializedName("email")
        private String email = "";
        @SerializedName("phoneNumber")
        private String phoneNumber = "";
        @SerializedName("photoUri")
        private String photoUri;
        @SerializedName("firebaseUid")
        private String firebaseUid = "";

        public PersonDTO() {
        }

        public PersonDTO(String name) {
            this.name = name;
        }
    }


}
