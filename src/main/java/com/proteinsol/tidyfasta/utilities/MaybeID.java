package com.proteinsol.tidyfasta.utilities;

public class MaybeID {

    private String idValue;
    private Boolean found = false;

    public MaybeID(){
       this.idValue = "Not Set";
    }

    public void setID(String id){
        this.idValue =  id;
        this.found = true;
    }

    public String getID(){
        return this.idValue;
    }

    public boolean testLine(String line) {
        if (line.startsWith(">")){
            setID(line);
            return true;
        } else {
            return false;
        }
    }

    public void empty() {
        this.idValue = "Not Set";
        this.found = false;
    }

    public boolean found() {
        if (this.idValue.length() == 1){
            return false;
        } else return this.found;
    }
}
