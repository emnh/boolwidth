package io;

import graph.VertexLabel;

import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;
import java.util.Map.Entry;

import boolwidth.BooleanDecomposition;

import com.thoughtworks.xstream.converters.MarshallingContext;
import com.thoughtworks.xstream.converters.collections.MapConverter;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;
import com.thoughtworks.xstream.mapper.Mapper;

@SuppressWarnings("serial")
public class AttributesConverter extends MapConverter {

	public static final Map<String, Class<?>> saveAttributes =
		new TreeMap<String, Class<?>>() {{
			// TODO: put these in a more visible position? inside Storage class?
			// TODO: different attributes converter for different classes
			// TODO: the class (2nd param) isn't used now, but can be used for type checking later
			put(BooleanDecomposition.BOOLWIDTH_FIELD, Integer.class);       // of decomposition
			put(BooleanDecomposition.BOOLWIDTH_TIME_FIELD, Long.class);  // time to find it
			put(VertexLabel.LABEL_FIELD, String.class);           // vertex
		}};

		public AttributesConverter(Mapper mapper) {
			super(mapper);
		}

		@SuppressWarnings("unchecked")
		@Override
		public void marshal(Object arg0, HierarchicalStreamWriter arg1,
				MarshallingContext arg2) {
			HashMap<String, Object> newmap = new HashMap<String, Object>();
			// TODO: check instanceof cloneable and clone instead of new HashMap to preserve type
			if (arg0 instanceof Map<?, ?>) {
				Map<String, Object> map = (Map<String, Object>) arg0;
				for (Entry<String, Object> e : map.entrySet()) {
					if (saveAttributes.containsKey(e.getKey())) {
						newmap.put(e.getKey(), e.getValue());
					}
				}
			}
			super.marshal(newmap == null ? arg0 : newmap, arg1, arg2);
			// TODO Auto-generated method stub
		}

		//	@Override
		//	public Object unmarshal(HierarchicalStreamReader arg0,
		//			UnmarshallingContext arg1) {
		//		// TODO Auto-generated method stub
		//		return null;
		//	}

		//	@Override
		//	public boolean canConvert(Class arg0) {
		//		// TODO Auto-generated method stub
		//		return false;
		//	}

}
