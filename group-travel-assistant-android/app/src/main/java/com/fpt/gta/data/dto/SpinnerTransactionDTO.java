package com.fpt.gta.data.dto;

import lombok.Data;

@Data
public class SpinnerTransactionDTO {
    private String txtSpinner;

    public SpinnerTransactionDTO(String txtSpinner) {
        this.txtSpinner = txtSpinner;
    }

    public String getTxtSpinner() {
        return txtSpinner;
    }

    public void setTxtSpinner(String txtSpinner) {
        this.txtSpinner = txtSpinner;
    }
}
