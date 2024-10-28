package com.serge.servlet;

import com.serge.dao.CurrencyDao;
import com.serge.entity.Currency;
import com.serge.service.CurrencyService;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.Optional;

@WebServlet("/currency/*")
public class CurrencyServlet extends HttpServlet {
    CurrencyService currencyService = CurrencyService.getInstance();
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        System.out.println();
        var currency = req.getPathInfo().substring(1);
        try {
            var result = currencyService.findCurrencyByCode(currency);
            resp.setStatus(200);
            resp.getWriter().write(result);
        } catch (RuntimeException e) {
            resp.setStatus(404);
            resp.getWriter().write(e.getMessage());
        }


    }
}
