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
	private String kwota;
	private String okres;
        private String procent;
	private Double result;

	@Inject
	FacesContext ctx;

	public String getKwota() {
		return kwota;
	}

	public void setKwota(String kwota) {
		this.kwota = kwota;
	}
        public String getProcent() {
		return procent;
	}

	public void setprocent(String procent) {
		this.procent = procent;
	}

	public Double getResult() {
		return result;
	}

	public void setResult(Double result) {
		this.result = result;
	}
        public String getOkres() {
		return okres;
	}

	public void setOkres(String okres) {
		this.okres = okres;
	}
	public String calc() {
		try {
			double kwota = Double.parseDouble(this.kwota);
			double okres = Double.parseDouble(this.okres);
                        double procent = Double.parseDouble(this.procent);

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
