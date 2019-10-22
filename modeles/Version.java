package modeles;

import java.util.Map;
import java.util.TreeMap;

public class Version { //TODO Mettre comparable<Version> plus tard
	private Long timestamp;
	private String name;
	private Map<Long, Version> alternativeVersions = new TreeMap<>();
	//TODO en plus de l'emploi du temps class TimeTable
	
	public Version (Long t, String str) {
		timestamp = t;
		name = str;
	}
	
	public String getName() {
		return name;
	}
	public Long getTimestamp() {
		return timestamp;
	}
	
	public Version addAltVer(Long t, String str) {
		Version ver = new Version(t, str);
		alternativeVersions.put(t, ver);
		return ver;
	}
	
	
	public boolean containsAltVer(Long t) { // Utile ou pas ?
		return alternativeVersions.containsKey(t);
	}
	
	public Version getVersion(Long t) {
		if(timestamp == t)
			return this;
		else
			return alternativeVersions.get(t);
	}
	
	public String toString() {
		StringBuilder sb = new StringBuilder ();
		sb.append(name + "\n");
		alternativeVersions.values().forEach(v -> {
			sb.append(name+"->"+v.toString());
		});
		return sb.toString();
	}
}
