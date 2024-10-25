package com.serge.servlet;

import com.serge.service.CurrencyService;
import com.serge.service.ExchangeRateService;
import com.serge.util.ErrorMessages;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.Collections;
import java.util.List;

@WebServlet("/exchangeRates")
public class ExchangeRatesServlet extends HttpServlet {
    private final ExchangeRateService ers = ExchangeRateService.getInstance();
    private final CurrencyService currencyService = CurrencyService.getInstance();

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        List<String> parameterNames = Collections.list(req.getParameterNames());
        if (!(parameterNames.contains("baseCurrencyCode")
                && parameterNames.contains("targetCurrencyCode")
                && parameterNames.contains("rate"))) {
            resp.setStatus(400);
            resp.setContentType("application/json");
            resp.setCharacterEncoding("UTF-8");
            resp.getWriter().write(ErrorMessages.NO_PARAMETER_EXCHANGE_RATE);
        } else {
            try {
                currencyService.findCurrencyByCode(req.getParameter("baseCurrencyCode"));
                currencyService.findCurrencyByCode(req.getParameter("targetCurrencyCode"));
                try {
                    ers.findRateByCodes(req.getParameter("baseCurrencyCode"),
                            req.getParameter("targetCurrencyCode"));
                    resp.setStatus(409);
                    resp.setContentType("application/json");
                    resp.setCharacterEncoding("UTF-8");
                    resp.getWriter().write(ErrorMessages.EXCHANGE_RATE_ALREADY_EXIST);
                } catch (Exception e) {
                    var result = ers.saveExchangeRate(
                            req.getParameter("baseCurrencyCode"),
                            req.getParameter("targetCurrencyCode"),
                            Double.valueOf(req.getParameter("rate"))
                    );
                    resp.setStatus(200);
                    resp.setContentType("application/json");
                    resp.setCharacterEncoding("UTF-8");
                    resp.getWriter().write(result);
                }
            } catch (Exception e) {
                resp.setStatus(404);
                resp.setContentType("application/json");
                resp.setCharacterEncoding("UTF-8");
                resp.getWriter().write(ErrorMessages.NO_CURRENCY);
            }
        }
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        var allExchangeRates = ers.findAllExchangeRates();
        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");
        resp.getWriter().write(allExchangeRates);
    }
}