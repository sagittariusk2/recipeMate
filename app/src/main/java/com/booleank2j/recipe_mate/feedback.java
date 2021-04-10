package com.booleank2j.recipe_mate;

public class feedback {
    private String email;
    private String feedback;
    private String rate;

    public feedback() {
    }

    public feedback(String Email, String Feedback, String Rate ) {

       this.email=Email;
       feedback=Feedback;
        rate=Rate;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getFeedback() {
        return feedback;
    }

    public void setFeedback(String Feedback) {
        feedback = Feedback;
    }

    public String getRate() {
        return rate;
    }

    public void setRate(String Rate) {
        rate = Rate;
    }
}

