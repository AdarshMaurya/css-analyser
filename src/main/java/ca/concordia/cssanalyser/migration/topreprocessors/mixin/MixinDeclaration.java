package ca.concordia.cssanalyser.migration.topreprocessors.mixin;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import ca.concordia.cssanalyser.cssmodel.declaration.Declaration;
import ca.concordia.cssanalyser.cssmodel.declaration.PropertyAndLayer;

public class MixinDeclaration {
	
	private String propertyName;
	private Map<PropertyAndLayer, MixinValue> mixinValues = new LinkedHashMap<>();
	private Declaration referenceDeclaration;
	private Set<Declaration> forDeclarations = new HashSet<>();
	private int mixinDeclarationNumber;

	public MixinDeclaration(String propertyName, Declaration referenceDeclaration, Iterable<Declaration> forDeclarations) {
		this.propertyName = propertyName;
		this.referenceDeclaration = referenceDeclaration;
		for (Declaration declaration : forDeclarations)
			this.forDeclarations.add(declaration);
	}
	
	public void addMixinValue(PropertyAndLayer propertyAndLayer, MixinValue value) {
		mixinValues.put(propertyAndLayer, value);
	}
	
	public Iterable<Declaration> getForDeclarations() {
		return forDeclarations;
	}
	
	public Declaration getReferenceDeclaration() {
		return this.referenceDeclaration;
	}
	
	public String getPropertyName() {
		return propertyName;
	}
	
	public MixinValue getMixinValueForPropertyandLayer(PropertyAndLayer propertyAndLayer) {
		return mixinValues.get(propertyAndLayer);
	}
	
	public Set<PropertyAndLayer> getAllSetPropertyAndLayers() {
		return mixinValues.keySet();
	}
	
	public double getAverageOfDeclarationsNumbers() {
		double sum = 0;
		for (Declaration declaration : forDeclarations)
			sum += declaration.getDeclarationNumber();
		return (double)sum / forDeclarations.size();
	}
	
	public int getMixinDeclarationNumber() {
		return mixinDeclarationNumber;
	}

	public void setMixinDeclarationNumber(int mixinDeclarationNumber) {
		this.mixinDeclarationNumber = mixinDeclarationNumber;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append(propertyName).append(System.lineSeparator());
		Map<Integer, List<PropertyAndLayer>> allLayers = new HashMap<>();
		for (PropertyAndLayer propertyAndLayer : mixinValues.keySet()) {
			List<PropertyAndLayer> propertyAndLayersForThislayer = allLayers.get(propertyAndLayer.getPropertyLayer());
			if (propertyAndLayersForThislayer == null) {
				propertyAndLayersForThislayer = new ArrayList<>();
				allLayers.put(propertyAndLayer.getPropertyLayer(), propertyAndLayersForThislayer);
			}
			propertyAndLayersForThislayer.add(propertyAndLayer);
		}
		
		for (int i = 1; i <= allLayers.size(); i++) {
			builder.append("(Layer ").append(i).append(") ");
			for (Iterator<PropertyAndLayer> propAndLayersIterator = allLayers.get(i).iterator(); propAndLayersIterator.hasNext(); ) {
				PropertyAndLayer propAndLayer = propAndLayersIterator.next();
				builder.append(propAndLayer.getPropertyName()).append(": ").append(mixinValues.get(propAndLayer));
				if (propAndLayersIterator.hasNext())
					builder.append(", ");
			}
			if (i < allLayers.size())
				builder.append(System.lineSeparator());
		}
		
		return builder.toString();
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((forDeclarations == null) ? 0 : forDeclarations.hashCode());
		result = prime * result
				+ ((mixinValues == null) ? 0 : mixinValues.hashCode());
		result = prime * result
				+ ((propertyName == null) ? 0 : propertyName.hashCode());
		result = prime
				* result
				+ ((referenceDeclaration == null) ? 0 : referenceDeclaration
						.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		MixinDeclaration other = (MixinDeclaration) obj;
		if (forDeclarations == null) {
			if (other.forDeclarations != null) {
				return false;
			}
		} else if (!forDeclarations.equals(other.forDeclarations)) {
			return false;
		}
		if (mixinValues == null) {
			if (other.mixinValues != null) {
				return false;
			}
		} else if (!mixinValues.equals(other.mixinValues)) {
			return false;
		}
		if (propertyName == null) {
			if (other.propertyName != null) {
				return false;
			}
		} else if (!propertyName.equals(other.propertyName)) {
			return false;
		}
		if (referenceDeclaration == null) {
			if (other.referenceDeclaration != null) {
				return false;
			}
		} else if (!referenceDeclaration.equals(other.referenceDeclaration)) {
			return false;
		}
		return true;
	}
	
	
}