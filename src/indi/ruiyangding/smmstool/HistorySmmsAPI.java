package indi.ruiyangding.smmstool;


public class HistorySmmsAPI implements SmmsAPI {
    public static String url = "https://sm.ms/api/list";

    private int domain;

    public int getDomain() {
        return domain;
    }

    public void setDomain(int domain) {
        this.domain = domain;
    }

    HistorySmmsAPI(int domain){
        this.domain = domain;
    }

    @Override
    public boolean sendRequest() {
        return false;
    }

}
