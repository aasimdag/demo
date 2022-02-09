package com.masparaga.demo.dal;

import com.masparaga.demo.model.Book;

public interface IBookDAL {
    Book findBookByName();
    Book findBookByAuthor();
    void addBook(Book book);
    void deleteBook(Book book);
    void updateBook(Book book);
}
