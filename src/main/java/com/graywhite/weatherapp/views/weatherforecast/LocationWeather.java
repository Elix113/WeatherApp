package com.graywhite.weatherapp.views.weatherforecast;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.graywhite.weatherapp.views.MainLayout;
import com.vaadin.flow.component.Composite;
import com.vaadin.flow.component.Key;
import com.vaadin.flow.component.KeyModifier;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.combobox.MultiSelectComboBox;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.GridVariant;
import com.vaadin.flow.component.icon.SvgIcon;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.renderer.ComponentRenderer;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.router.RouteAlias;

import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.Set;

import jakarta.validation.constraints.NotNull;

import org.vaadin.lineawesome.LineAwesomeIcon;

/**
 * LocationWeather
 * @author Elias Weissensteiner
 * @since 2024-06-11
 */
@PageTitle("LocationWeather")
@Route(value = "location", layout = MainLayout.class)
@RouteAlias(value = "location", layout = MainLayout.class)
public class LocationWeather extends Composite<VerticalLayout> {

	private Grid<JsonNode> grid = new Grid<>();

	public LocationWeather() {
		TextField tfLocation = createLocationTF();
		MultiSelectComboBox<Locale> multiSelCBoxCountry = createCountryMultiSelCBox();

		tfLocation.addValueChangeListener(e -> updateGrid(e.getValue(), multiSelCBoxCountry.getValue()));
		multiSelCBoxCountry.addValueChangeListener(e -> updateGrid(tfLocation.getValue(), e.getValue()));

		grid.addColumn(e -> e.get("display_name").asText())
			.setHeader("Name")
			.setSortable(true);
		grid.addColumn(e -> e.get("lon").asDouble())
			.setHeader("Längengrad")
			.setAutoWidth(true).setFlexGrow(0)
			.setSortable(true)
			.setComparator(Comparator.comparingDouble(e -> e.get("lon").asDouble()));
		grid.addColumn(e -> e.get("lat").asDouble())
			.setHeader("Breitengrad")
			.setAutoWidth(true).setFlexGrow(0)
			.setSortable(true)
			.setComparator(Comparator.comparingDouble(e -> e.get("lat").asDouble()));
		grid.addColumn(createLinkIcon())
			.setAutoWidth(true).setFlexGrow(0);
		grid.addThemeVariants(GridVariant.LUMO_WRAP_CELL_CONTENT);

		HorizontalLayout hl = new HorizontalLayout(tfLocation, multiSelCBoxCountry);
		hl.getStyle().setWidth("100%").setFlexGrow("1");
		getContent().add(
			hl,
			grid
		);
	}



	private static List<JsonNode> getLocations(@NotNull String query, String countryCodes) {
		JsonNode arrayNode;
		String u = "https://nominatim.openstreetmap.org/search?format=jsonv2&limit=50&q="
			+ URLEncoder.encode(query, StandardCharsets.UTF_8);
		if (countryCodes != null)
			u += countryCodes;
		try {
			URL url = new URL(u);
			ObjectMapper objectMapper = new ObjectMapper();
			arrayNode = objectMapper.readTree(url);
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
		ArrayList<JsonNode> locations = null;
		if (arrayNode != null && arrayNode.isArray()) {
			locations = new ArrayList<>();
			for (JsonNode node : arrayNode) {
				locations.add(node);
			}
		}
		return locations;
	}

	private void updateGrid(String searchTerm, Set<Locale> locales) {
		if (!searchTerm.isEmpty()) {
			if (!locales.isEmpty()) {
				StringBuilder countryCodes = new StringBuilder("&countrycodes=");
				locales.forEach(i -> countryCodes.append(i.getCountry()).append(","));
				grid.setItems(getLocations(searchTerm, countryCodes.toString()));
			}
			else
				grid.setItems(getLocations(searchTerm, null));
			grid.recalculateColumnWidths();
		}
	}

	private static ComponentRenderer<SvgIcon, JsonNode> createLinkIcon() {
		return new ComponentRenderer<>(LineAwesomeIcon.GLOBE_SOLID::create, (i, n) -> {
			String s = "https://www.google.com/maps/search/?api=1&query=" + n.get("lat").asText() + "," + n.get("lon").asText();
			i.addClickListener(e -> UI.getCurrent().getPage().open(s, "_blank"));
			i.getStyle().set("cursor", "pointer");
		});
	}

	private static TextField createLocationTF() {
		var tfLocation = new TextField("Standort");
		tfLocation.setClearButtonVisible(true);
		tfLocation.setPrefixComponent(LineAwesomeIcon.MAP_MARKER_SOLID.create());
		tfLocation.addFocusShortcut(Key.KEY_F, KeyModifier.CONTROL);
		tfLocation.setPlaceholder("Ort eingeben");
		tfLocation.getStyle().setFlexGrow("1");
		return tfLocation;
	}

	private MultiSelectComboBox<Locale> createCountryMultiSelCBox() {
		var multiSelCBoxCountry = new MultiSelectComboBox<Locale>("Länder");
		List<Locale> countries = Arrays.stream(Locale.getISOCountries()).map(i -> Locale.of("", i)).toList();
		multiSelCBoxCountry.setItems(countries);
		multiSelCBoxCountry.setItemLabelGenerator(Locale::getDisplayCountry);
		multiSelCBoxCountry.setAllowCustomValue(false);
		multiSelCBoxCountry.setAutoExpand(MultiSelectComboBox.AutoExpandMode.HORIZONTAL);
		multiSelCBoxCountry.setPlaceholder("Auf Länder beschränken");
		multiSelCBoxCountry.getStyle().setFlexGrow("1");
		return multiSelCBoxCountry;
	}
}
