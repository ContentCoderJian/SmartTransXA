package cn.com.jian.smarttransxa.domain;

/**
 * bus µÃÂ¿‡
 * 
 * @author Jian
 * 
 */
public class Bus {

	public Integer _id;
	private String line;
	private String time;
	private String station;
	private String opposite;

	public Bus() {
		super();
	}

	public Bus(String line, String time, String station, String opposite) {
		super();
		this.line = line;
		this.time = time;
		this.station = station;
		this.opposite = opposite;
	}

	public Integer get_id() {
		return _id;
	}

	public void set_id(Integer _id) {
		this._id = _id;
	}

	public String getLine() {
		return line;
	}

	public void setLine(String line) {
		this.line = line;
	}

	public String getTime() {
		return time;
	}

	public void setTime(String time) {
		this.time = time;
	}

	public String getStation() {
		return station;
	}

	public void setStation(String station) {
		this.station = station;
	}

	public String getOpposite() {
		return opposite;
	}

	public void setOpposite(String opposite) {
		this.opposite = opposite;
	}

	@Override
	public String toString() {
		return "_id=" + _id + "\nline=" + line + "\ntime=" + time
				+ "\nstation=" + station + "\nopposite=" + opposite;
	}
}
