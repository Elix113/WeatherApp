package com.graywhite.weatherapp.views.weatherforecast;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.graywhite.weatherapp.DaySForecast;
import com.graywhite.weatherapp.views.MainLayout;
import com.vaadin.flow.component.Composite;
import com.vaadin.flow.component.Key;
import com.vaadin.flow.component.KeyModifier;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.tabs.Tab;
import com.vaadin.flow.component.tabs.TabSheet;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.router.RouteAlias;

import java.net.URL;
import java.util.ArrayList;
import java.util.Date;

@PageTitle("SouthTyrolWeather")
@Route(value = "southtyrol", layout = MainLayout.class)
@RouteAlias(value = "southtyrol", layout = MainLayout.class)
public class SouthTyrolWeather extends Composite<VerticalLayout> {


	public SouthTyrolWeather() {
		HorizontalLayout hl = new HorizontalLayout();
		ArrayList<Pair<String, String>> municipalities = getMunicipalitiesWithID();
		ComboBox<Pair<String, String>> selMunicipality = createMunicipalityComboBox(municipalities);
		DaySForecast days = new DaySForecast(selMunicipality.getValue().s);
		selMunicipality.addValueChangeListener(e -> days.update(selMunicipality.getValue().s));

		hl.add(
			selMunicipality
		);

		TabSheet tabs = new TabSheet();
		Tab t = new Tab("Day Forecast");
		tabs.add(t, days);
		tabs.add("Hourly Forecast", new Span("ztztztztfz"));

		tabs.getStyle().set("width", "100%");

		getContent().setWidth("100%");
		getContent().getStyle().set("flex-grow", "1");
		hl.setWidth("100%");
		hl.getStyle().setFlexGrow("1");
		getContent().add(hl);
		getContent().add(tabs);
	}

	private static ComboBox<Pair<String, String>> createMunicipalityComboBox(ArrayList<Pair<String, String>> municipalities)
	{
		var sel = new ComboBox<Pair<String, String>>();
		sel.setLabel("Gemeinde");
		sel.setItems(municipalities);
		sel.setItemLabelGenerator(Pair::f);
		sel.setValue(municipalities.getFirst());
		sel.setAllowCustomValue(false);
		sel.addFocusShortcut(Key.KEY_F, KeyModifier.CONTROL);
		sel.getStyle().setFlexGrow("1");
		return sel;
	}

	private static ArrayList<Pair<String, String>> getMunicipalitiesWithID() {
		JsonNode arrayNode;
		try {
			URL url = new URL("https://tourism.api.opendatahub.com/v1/Municipality");
			ObjectMapper objectMapper = new ObjectMapper();
			arrayNode = objectMapper.readTree(url);
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}

		ArrayList<Pair<String, String>> municipalities = null;
		if (arrayNode != null && arrayNode.isArray()) {
			municipalities = new ArrayList<>();
			for (JsonNode jsonNode : arrayNode) {
				String municipalityID = jsonNode.get("IstatNumber").asText();
				String langDe = jsonNode.get("Detail").get("de").get("Title").asText();
				municipalities.add(new Pair<>(langDe, municipalityID));
			}
		}

		return municipalities;
	}

	private record Pair<F, S>(F f, S s) {}

	public record WeatherForecastEntry(Date date, int minTemp, int maxTemp, int pp, char wCode, int sunshineDuration,
		String imageUrl) {}

}
