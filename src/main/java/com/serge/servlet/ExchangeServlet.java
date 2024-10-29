package com.serge.servlet;

import com.serge.service.CurrencyService;
import com.serge.service.ExchangeService;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.math.BigDecimal;

@WebServlet("/exchange")
public class ExchangeServlet extends HttpServlet {
    ExchangeService exchangeService = ExchangeService.getINSTANCE();
    CurrencyService currencyService = CurrencyService.getInstance();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        var from = req.getParameter("from");
        var to = req.getParameter("to");
        var amount = req.getParameter("amount");
        if (from == null || to == null || amount == null) {
            resp.setContentType("text/plain");
            resp.getWriter().write("Нет нужного параметра");
        } else {
            try {
                try {
                    currencyService.findCurrencyByCode(from);
                    currencyService.findCurrencyByCode(to);
                } catch (RuntimeException e) {
                    resp.setContentType("text/plain");
                    resp.getWriter().write("Параметры from/to должны содержать код валюты (USD, RUB etc.");
                }
                var v = new BigDecimal(amount);
                var s = exchangeService.doExchange(from, to, v);
                resp.getWriter().write(s);
            } catch (NumberFormatException e) {
                resp.setContentType("text/plain");
                resp.getWriter().write("amount должно быть числом");
            } catch (RuntimeException e) {
                resp.getWriter().write(e.getMessage());
            }

        }
    }
}
