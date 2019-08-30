package gov.nist.healthcare.iz.darq.digest.domain;

import java.util.HashMap;
import java.util.Map;
import org.apache.commons.collections4.bidimap.DualHashBidiMap;

public class TransformResult<A, B, C> {
	
	public DualHashBidiMap<String, String> vaxID;
	private C payload;
	public Map<String, A> typeA;
	public Map<String, B> typeB;
	
	public TransformResult() {
		super();
		vaxID = new DualHashBidiMap<>();
		typeA = new HashMap<>();
		typeB = new HashMap<>();
	}
	
	public void add(String a, String b, A aa, B bb){
		vaxID.put(a, b);
		typeA.put(a, aa);
		typeB.put(b, bb);
	}
	
	public B getBFromA(String a){
		String id = vaxID.get(a);
		return typeB.get(id);
	}
	
	public A getAFromB(String b){
		String id = vaxID.inverseBidiMap().get(b);
		return typeA.get(id);
	}

	public C getPayload() {
		return payload;
	}

	public void setPayload(C payload) {
		this.payload = payload;
	}
	
}
