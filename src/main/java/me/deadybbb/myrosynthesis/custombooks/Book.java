package me.deadybbb.myrosynthesis.custombooks;

import me.deadybbb.myrosynthesis.customeffects.Effect;

import java.util.List;

public class Book {
    public String bookId;
    public String title;
    public String author;
    public String content;
    public String receiveMessage;
    public List<String> effects;

    public Book(String bookId, String title, String author, String content, String receiveMessage, List<String> effects) {
        this.bookId = bookId;
        this.title = title;
        this.author = author;
        this.content = content;
        this.receiveMessage = receiveMessage;
        this.effects = effects;
    }
}
