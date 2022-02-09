package com.masparaga.demo.dal;

import com.masparaga.demo.model.Book;
import org.springframework.stereotype.Component;

@Component
public class BookDAL implements IBookDAL{

    @Override
    public Book findBookByName() {
        return null;
    }

    @Override
    public Book findBookByAuthor() {
        return null;
    }

    @Override
    public void addBook(Book book) {

    }

    @Override
    public void deleteBook(Book book) {

    }

    @Override
    public void updateBook(Book book) {

    }
}
