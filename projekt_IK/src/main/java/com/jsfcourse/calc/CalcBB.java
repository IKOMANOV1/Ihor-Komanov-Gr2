package com.jsfcourse.calc;

import jakarta.inject.Inject;
import jakarta.inject.Named;
import jakarta.enterprise.context.RequestScoped;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;

@Named
@RequestScoped
//@SessionScoped
public class CalcBB {
	private Double kwota;
	private Double okres;
        private Double procent;
	private Double result;

	@Inject
	FacesContext ctx;

	public Double getKwota() {
		return kwota;
	}

	public void setKwota(Double kwota) {
		this.kwota = kwota;
	}
        public Double getProcent() {
		return procent;
	}

	public void setprocent(Double procent) {
		this.procent = procent;
	}

	public Double getResult() {
		return result;
	}

	public void setResult(Double result) {
		this.result = result;
	}
        public Double getOkres() {
		return okres;
	}

	public void setOkres(Double okres) {
		this.okres = okres;
	}
	public String calc() {
		try {
			result =  ((procent / 100 * kwota) * okres) + kwota;

			ctx.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Operacja wykonana poprawnie", null));
			return "showresult"; 
		} catch (Exception e) {
			ctx.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Błąd podczas przetwarzania parametrów", null));
			return null; 
		}
	}


	public String info() {
		return "info";
	}
}
