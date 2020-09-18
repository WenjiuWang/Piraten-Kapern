package com.wenjiuwang.PiratenKapern;

public class Gamedata implements java.io.Serializable  {
	private static final long serialVersionUID = 1L;
	public RequestCode code;
	public Fortune fortune;
	public int fortuneIndicator;
	public int[] data;
	public int score;
	
	public Gamedata(RequestCode c, Fortune f, int n, int[] d, int s) {
		this.code = c;
		this.fortune = f;
		this.fortuneIndicator = n;
		this.data = d.clone();
		this.score = s;
	}
}
