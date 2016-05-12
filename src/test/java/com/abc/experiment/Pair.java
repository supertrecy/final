package com.abc.experiment;

public class Pair {
	int id1;
	int id2;

	public Pair(int id1, int id2) {
		super();
		this.id1 = id1;
		this.id2 = id2;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + id1;
		result = prime * result + id2;
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
		Pair other = (Pair) obj;
		if (other.id1 != this.id1 && other.id1 != this.id2)
			return false;
		if (other.id2 != this.id1 && other.id2 != this.id2)
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "[" + id1 + "," + id2 + "]";
	}
}
