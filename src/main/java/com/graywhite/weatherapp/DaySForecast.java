package com.graywhite.weatherapp;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.graywhite.weatherapp.views.weatherforecast.SouthTyrolWeather;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;

import java.net.URL;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang3.time.DateUtils;

/**
 * CmpFiveDayForecast
 * @author Elias Weissensteiner
 * @since 2024-06-06
 */
public class DaySForecast extends HorizontalLayout {


	private ArrayList<SouthTyrolWeather.WeatherForecastEntry> entries;
	private List<CmpWeatherDayCard> cards;
	private String id;

	public DaySForecast(String id) {
		this.id = id;
		getStyle().set("flex-wrap", "wrap");
		update();
	}

	private ArrayList<SouthTyrolWeather.WeatherForecastEntry> getFiveDays(String id) {
		JsonNode arrayNode;
		try {
			URL url = new URL("https://tourism.opendatahub.com/v1/Weather/Forecast/forecast_" + id);
			ObjectMapper objectMapper = new ObjectMapper();
			arrayNode = objectMapper.readTree(url).get("ForeCastDaily");
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
		ArrayList<SouthTyrolWeather.WeatherForecastEntry> days = null;
		if (arrayNode != null && arrayNode.isArray()) {
			days = new ArrayList<>();
			for (JsonNode node : arrayNode) {
				OffsetDateTime offsetDateTime = OffsetDateTime.parse(node.get("Date").asText());
				Date date = Date.from(offsetDateTime.toInstant());
				int minTemp = node.get("MinTemp").asInt();
				int maxTemp = node.get("MaxTemp").asInt();
				int precipitationProbability = node.get("PrecipitationProbability").asInt();
				int sunshineDuration = node.get("SunshineDuration").asInt();
				String imageUrl = node.get("WeatherImgUrl").asText();
				String wCode = node.get("WeatherCode").asText();
				if (!wCode.equals("null"))
					days.add(new SouthTyrolWeather.WeatherForecastEntry(date, minTemp, maxTemp,
						precipitationProbability, wCode.charAt(0), sunshineDuration, imageUrl
					));
			}
		}
		return days;
	}

	public void setID(String id) {
		this.id = id;
	}

	public void update(String id) {
		this.id = id;
		update();
	}

	private void update() {
		cards = new ArrayList<>();
		removeAll();
		entries = getFiveDays(id);
		for (SouthTyrolWeather.WeatherForecastEntry e : entries) {
			if(!DateUtils.isSameDay(e.date(), DateUtils.addDays(new Date(), -1))) {
				CmpWeatherDayCard c = new CmpWeatherDayCard(e.date(), e.minTemp(), e.maxTemp(), e.pp(), e.wCode(),
					e.sunshineDuration(), e.imageUrl());
				cards.add(c);
				add(c);
			}
		}
	}
}
