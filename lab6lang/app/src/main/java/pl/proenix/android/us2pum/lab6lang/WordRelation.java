package pl.proenix.android.us2pum.lab6lang;

/**
 * Class wrap for WordRelation object.
 * Both way relation should be declared if needed.
 */
public class WordRelation {
    private int _id;
    private int _word_1_id;
    private int _word_2_id;

    public WordRelation() {}

    public WordRelation(int id, int id_word_1, int id_word_2) {
        this._id = id;
        this._word_1_id = id_word_1;
        this._word_2_id = id_word_2;
    }

    public WordRelation(int id_word_1, int id_word_2) {
        this._word_1_id = id_word_1;
        this._word_2_id = id_word_2;
    }

    public int getID() {
        return this._id;
    }

    public void setID(int id) {
        this._id = id;
    }

    public int getWord1ID() {
        return this._word_1_id;
    }
    public int getWord2ID() {
        return this._word_2_id;
    }

    public void setWord1ID(int id) {
        this._word_1_id = id;
    }

    public void setWord2ID(int id) {
        this._word_2_id = id;
    }

}
