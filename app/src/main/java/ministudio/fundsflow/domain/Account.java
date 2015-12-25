package ministudio.fundsflow.domain;

import com.google.common.base.Strings;

/**
 * Created by min on 15/12/25.
 */
public class Account {

    private int _id;
    private String _name = null;
    private float _balance = 0;

    public Account(int id, String name, float balance) {
        if (Strings.isNullOrEmpty(name)) {
            throw new IllegalArgumentException("The argument is required - name");
        }
        if (balance < 0) {
            throw new IllegalArgumentException("The argument must be more than 0 - balance");
        }
        this._id = id;
        this._name = name;
        this._balance = balance;
    }

    public int getId() {
        return this._id;
    }

    public void setName(String name) {
        if (Strings.isNullOrEmpty(name)) {
            throw new IllegalArgumentException("The argument is required - name");
        }
        this._name = name;
    }

    public String getName() {
        return this._name;
    }

    public float getBalance() {
        return this._balance;
    }

    public void save() {
        // Todo: persist account
    }

    public static Account getAccount(int id) {
        return null;
    }

    public static Account[] getAccounts() {
        return null;
    }
}
