import java.util.Optional;

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

    //if an item is passed in we print more specific information about the tag that was read.
    //Optional as some tags read do not correspond to items
    public String toString(Optional<D1Item> item)
    {
        if(item.isPresent()){
            return item.get().getName() + "," + item.get().getBarcode() + "," + item.get().getPrice() + "," + this.rssi;
        }else {
            return this.epc + ",N/A,N/A," + this.rssi;
        }
    }
}
