public class D1Tag {
    private int rssi;
    private String epc;

    public int getRssi() {
        return rssi;
    }

    public void setRssi(int rssi) {
        this.rssi = rssi;
    }

    public String getEpc() {
        return epc;
    }

    public void setEpc(String epc) {
        this.epc = epc;
    }

    public D1Tag(int rssi, String epc){
        this.rssi = rssi;
        this.epc = epc;
    }

    public String toString(){
        return this.epc + "," + this.rssi;
    }
}
