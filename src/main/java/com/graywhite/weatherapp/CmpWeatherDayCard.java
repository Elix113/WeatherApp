package com.graywhite.weatherapp;

import com.graywhite.weatherapp.views.weatherforecast.SouthTyrolWeather;
import com.vaadin.flow.component.html.Hr;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.SvgIcon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;

import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.commons.lang3.time.DateUtils;
import org.vaadin.lineawesome.LineAwesomeIcon;

/**
 * CmpCard
 * @author Elias Weissensteiner
 * @since 2024-06-07
 */

public class CmpWeatherDayCard extends VerticalLayout {

	private Date date;
	private int minTemp;
	private int maxTemp;
	private int precipitationProbability;
	private char wCode;
	private int sunshineDuration;
	private String imageUrl;
	private boolean ofDay;

	public CmpWeatherDayCard(Date date, int minTemp, int maxTemp, int precipitationProbability, char wCode,
		int sunshineDuration, String imageUrl)
	{
		this.date = date;
		this.minTemp = minTemp;
		this.maxTemp = maxTemp;
		this.precipitationProbability = precipitationProbability;
		this.wCode = wCode;
		this.sunshineDuration = sunshineDuration;
		this.imageUrl = imageUrl;
		create();
	}

	public void create() {
		Span date;
		if (DateUtils.isSameDay(this.date, DateUtils.addDays(new Date(), 0))) {
			date = new Span("Heute");
		}
		else if (DateUtils.isSameDay(this.date, DateUtils.addDays(new Date(), 1))) {
			date = new Span("Morgen");
		}
		else if (DateUtils.isSameDay(this.date, DateUtils.addDays(new Date(), -1))) {
			date = new Span("Gestern");
		}
		else {
			SimpleDateFormat dateFormat = new SimpleDateFormat("EEEE, dd MMMM yyyy");
			date = new Span(dateFormat.format(this.date));
		}
		date.getStyle().set("text-align", "center");

		Image image = new Image(imageUrl, "");
		image.setWidth("90px");
		image.setHeight("90px");

		Icon d = VaadinIcon.DROP.create();
		d.setColor("#0563b6");
		SvgIcon s =  LineAwesomeIcon.SUN_SOLID.create();
		s.setColor("#ffd500");

		HorizontalLayout pp = new HorizontalLayout(d, new Span(precipitationProbability + "%"));
		HorizontalLayout sun = new HorizontalLayout(s, new Span(sunshineDuration + "h"));

		setDefaultHorizontalComponentAlignment(FlexComponent.Alignment.CENTER);
		setJustifyContentMode(FlexComponent.JustifyContentMode.CENTER);
		add(
			new HorizontalLayout(pp, sun),
			image,
			new Span(minTemp + "°C - " + maxTemp + "°C"),
			new Hr(),
			date
		);
		getStyle()
			.set("padding", "15px")
			.set("border-radius", "15px")
			.set("background", "#33373c")
			.set("width", "auto");
	}

	public void update(SouthTyrolWeather.WeatherForecastEntry e) {
		this.date = e.date();
		this.minTemp = e.minTemp();
		this.maxTemp = e.maxTemp();
		this.precipitationProbability = e.pp();
		this.wCode = e.wCode();
		this.sunshineDuration = e.sunshineDuration();
		this.imageUrl = e.imageUrl();
		update();
	}

	private void update() {
		removeAll();
		create();
	}
}
