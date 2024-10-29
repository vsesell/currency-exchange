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
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@WebServlet("/exchangeRate/*")
public class ExchangeRateServlet extends HttpServlet {
    private final ExchangeRateService exchangeRateService = ExchangeRateService.getInstance();
    private final CurrencyService currencyService = CurrencyService.getInstance();


    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        var query = req.getPathInfo().substring(1);
        if (query.length() != 6) {
            resp.getWriter().write(ErrorMessages.SIX_CHARACTER);
        } else {
            String[] queryArray = {query.substring(0, query.length() / 2), query.substring(query.length() / 2)};
            try {
                currencyService.findCurrencyByCode(queryArray[0]);
                currencyService.findCurrencyByCode(queryArray[1]);
                try {
                    var rateByCodes = exchangeRateService.findRateByCodes(queryArray[0], queryArray[1]);
                    resp.setStatus(200);
                    resp.getWriter().write(rateByCodes);
                } catch (Exception e) {
                    resp.setStatus(404);
                    resp.getWriter().write(ErrorMessages.NO_EXCHANGE_RATE);
                }
            } catch (RuntimeException e) {
                resp.setStatus(400);
                resp.getWriter().write(ErrorMessages.NO_EXCHANGE_RATE);
            }
        }

    }


    @Override
    protected void service(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        if (req.getMethod().equalsIgnoreCase("PATCH")){
            doPatch(req, resp);
        } else {
            super.service(req, resp);
        }
    }

    protected void doPatch(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        var query = req.getPathInfo().substring(1);
        List<String> params = Collections.list(req.getParameterNames());
        if (query.length() != 6) {
            resp.getWriter().write(ErrorMessages.SIX_CHARACTER);
        } else if (!params.contains("rate")) {
            resp.setStatus(400);
            resp.getWriter().write(ErrorMessages.NO_PARAMETER_RATE);
        } else {
            String[] queryArray = {query.substring(0, query.length() / 2), query.substring(query.length() / 2)};
            try {
                exchangeRateService.findRateByCodes(queryArray[0], queryArray[1]);
                var rate = exchangeRateService.updateExchangeRate(queryArray[0], queryArray[1], new BigDecimal(req.getParameter("rate")));
                resp.setStatus(200);
                resp.getWriter().write(rate);
            } catch (Exception e) {
                resp.setStatus(404);
                resp.getWriter().write(ErrorMessages.NO_EXCHANGE_RATE);
            }
        }
    }
}
