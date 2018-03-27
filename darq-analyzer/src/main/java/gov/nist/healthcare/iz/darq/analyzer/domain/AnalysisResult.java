package gov.nist.healthcare.iz.darq.analyzer.domain;

import java.util.ArrayList;
import java.util.List;

import gov.nist.healthcare.iz.darq.analyzer.domain.AnalysisQuery.QueryField;

public class AnalysisResult {
	public static class Node<T> {
		T value;
		List<Node<?>> children;
		
		public Node() {
			super();
			children = new ArrayList<>();
		}

		public boolean isLeaf() {
			return children.size() == 0;
		}
		public List<Node<?>> getChildren() {
			return children;
		}
		public void setChildren(List<Node<?>> children) {
			this.children = children;
		}
		public T getValue() {
			return value;
		}

		public void setValue(T value) {
			this.value = value;
		}

		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + ((value == null) ? 0 : value.hashCode());
			return result;
		}

		@Override
		public boolean equals(Object obj) {
			if (this == obj)
				return true;
			if (obj == null)
				return false;
			if (getClass() != obj.getClass())
				return false;
			Node other = (Node) obj;
			if (value == null) {
				if (other.value != null)
					return false;
			} else if (!value.equals(other.value))
				return false;
			return true;
		}

		@Override
		public String toString() {
			return "Node [value=" + value + ", children=" + children + "]";
		}
		
	}
	
	List<Field> header;
	List<Node<?>> nodes;

	public AnalysisResult(List<Field> header) {
		super();
		this.header = header;
		this.nodes = new ArrayList<>();
	}
	
	public void addTrays(List<Tray> ts){
		System.out.println(header);
		for(Tray t : ts){
//			System.out.println(t);
			addTray(t, header, 0, nodes);
		}	
	}
	
	public void addTray(Tray t, List<Field> headers, int i, List<Node<?>> l){
		if(i < headers.size()){
			Field field = headers.get(i);
			String str = t.get(field);
			Node<?> existing = l.stream().filter(x -> x.getValue().equals(str)).findFirst().orElse(null);
			if(existing == null){
				Node<String> node = new Node<>();
				node.setValue(str);
				l.add(node);
				addTray(t, headers, i+1, node.children);
			}
			else {
				addTray(t, headers, i+1, existing.children);
			}
		}
		else {
			if(l.size() == 1){
				((Node<Double>) l.get(0)).setValue(((Double) l.get(0).getValue() + t.getCount()));
			}
			else {
				Node<Double> node = new Node<Double>();
				node.setValue((double) t.getCount()); 
				l.add(node);
			}
		}
	}

	public List<Node<?>> getNodes() {
		return nodes;
	}

	public void setNodes(List<Node<?>> nodes) {
		this.nodes = nodes;
	}

	@Override
	public String toString() {
		return "AnalysisResult [header=" + header + ", nodes=" + nodes + "]";
	}
	
	

	
}
