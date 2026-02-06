package com.generation.ateneo.entities;

import java.lang.reflect.Field;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

//Interfaccia che fornisce dei metodi che permettono la costruzione di chi la implementa partendo da una
//mappa con coppie chiave-valore rappresentative dell'oggetto stesso da costriure
public interface IMappable {

	public default void fromMap(Map<String, String> params)
			throws NumberFormatException, IllegalArgumentException, IllegalAccessException {
		// this nell'interfaccia fa riferimento all'oggetto (istanza) stesso che
		// chiamerà questo metodo.
		/*
		 * params =
		 * {
		 * "nome" : "Mario",
		 * "cognome" : "Rossi",
		 * "dataNascita" : "1998-08-23",
		 * "coniugato" : "true"
		 * }
		 * 
		 */

		/*
		 * //Prendo tutti i campi privati, pubblici o protected della classe stessa
		 * //(Attenzione cosi non ottengo quelli ereditati)
		 * Field[] fields = this.getClass().getDeclaredFields();
		 * 
		 * //Rendo accessibili i campi che ho trovato in modo da poterli manipolare
		 * for(Field f : fields) {
		 * f.setAccessible(true);
		 * }
		 */

		// Ottengo tutti i campi considerando anche quelli nelle superclassi grazie al
		// metodo utile di questa interfaccia getAllFields()
		List<Field> fields = getAllFields(this.getClass());

		// Entry<String, String> è la rappresentazione di una qualsiasi coppia presente
		// nella mia mappa 'params'
		// .entrySet() mi restituisce un Set<Entry<String, String>> un set iterabile di
		// coppie String, String della mappa 'params'

		for (Entry<String, String> coppia : params.entrySet()) {
			String key = coppia.getKey();
			String value = coppia.getValue();

			for (Field field : fields) {

				if (key.equals(field.getName())) {
					// String -> string | Integer -> integer | LocalDate -> localdate
					// String fieldType = field.getType().getSimpleName().toLowerCase();
					if (value == null) {
						break;
					}

					if (field.getType() == Integer.class || field.getType() == int.class) {
						field.setInt(this, Integer.parseInt(value));
					} else if (field.getType() == Double.class || field.getType() == double.class) {
						field.setDouble(this, Double.parseDouble(value));
					} else if (field.getType() == Float.class || field.getType() == float.class) {
						field.setFloat(this, Float.parseFloat(value));
					} else if (field.getType() == Boolean.class || field.getType() == boolean.class) {
						field.setBoolean(this,
								value.equalsIgnoreCase("true") ||
										value.equalsIgnoreCase("si") ||
										value.equalsIgnoreCase("yes") ||
										value.equalsIgnoreCase("1"));
					} else if (field.getType() == String.class) {
						field.set(this, value);
					} else if (field.getType() == LocalDate.class) {
						field.set(this, LocalDate.parse(value));
					} else if (field.getType() == Long.class) {
						field.set(this, Long.parseLong(value));
					} else if (field.getType() == LocalDateTime.class) {
						field.set(this, LocalDateTime.parse(value));
					} else if (field.getType() == Instant.class) {
						field.set(this, Instant.parse(value));
					} else {

						// System.out.println("Errore per il campo: " + field.getName() + " di tipo: " +
						// field.getType().getSimpleName());
					}

				}
			}
		}
	}

	public default Map<String, String> toMap() throws IllegalArgumentException, IllegalAccessException {
		Map<String, String> params = new HashMap<String, String>();

		List<Field> fields = getAllFields(this.getClass());

		for (Field field : fields) {
			String fieldName = field.getName();
			String fieldType = field.getType().getSimpleName().toLowerCase();

			switch (fieldType) {
				case "int", "integer":
					Integer intValue = field.getInt(this);
					params.put(fieldName, String.valueOf(intValue));
					break;
				case "double":
					Double doubleValue = field.getDouble(this);
					params.put(fieldName, String.valueOf(doubleValue));
					break;
				case "float":
					Float floatValue = field.getFloat(this);
					params.put(fieldName, String.valueOf(floatValue));
					break;
				case "boolean":
					Boolean boolValue = field.getBoolean(this);
					params.put(fieldName, String.valueOf(boolValue));
					break;
				case "string":
					String strValue = (String) field.get(this);
					params.put(fieldName, strValue);
					break;
				case "localdate":
					LocalDate dateValue = (LocalDate) field.get(this);
					params.put(fieldName, dateValue.toString());
					break;
				case "long":
					Long longValue = (Long) field.get(this);
					params.put(fieldName, String.valueOf(longValue));
					break;
				case "localdatetime":
					LocalDateTime dataTimeValue = (LocalDateTime) field.get(this);
					params.put(fieldName, String.valueOf(dataTimeValue));
					break;
				case "instance":
					Instant timeValue = (Instant) field.get(this);
					params.put(fieldName, String.valueOf(timeValue));
					break;
				default:
					System.out.println(
							"Errore per il campo: " + field.getName() + " di tipo: " + field.getType().getSimpleName());
					break;
			}
		}

		return params;
	}

	private List<Field> getAllFields(Class<?> type) {
		List<Field> allField = new ArrayList<Field>();

		while (type != null && type != Object.class) {
			Arrays.stream(type.getDeclaredFields())
					.forEach(
							field -> {
								field.setAccessible(true);
								allField.add(field);
							});
			type = type.getSuperclass();
		}

		return allField;
	}

}
