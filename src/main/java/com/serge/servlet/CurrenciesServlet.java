package com.serge.servlet;

import com.serge.service.CurrencyService;
import com.serge.util.ErrorMessages;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.Collections;

@WebServlet("/currencies")
public class CurrenciesServlet extends HttpServlet {
    private static final CurrencyService currencyService = CurrencyService.getInstance();

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        var parameterNames = Collections.list(req.getParameterNames());
            if (!(parameterNames.contains("name") && parameterNames.contains("code") &&
                    parameterNames.contains("sign"))) {
                resp.setStatus(400);
                resp.setContentType("application/json");
                resp.setCharacterEncoding("UTF-8");
                resp.getWriter().write(ErrorMessages.NO_PARAMETER_CURRENCY);
            } else {

                try {
                    var name = currencyService.findCurrencyByCode(req.getParameter("code"));
                    resp.setStatus(409);
                    resp.setContentType("application/json");
                    resp.setCharacterEncoding("UTF-8");
                    resp.getWriter().write(ErrorMessages.CURRENCY_ALREADY_EXIST);

                } catch (RuntimeException e) {
                    var result = currencyService.saveCurrency(req.getParameter("code"), req.getParameter("name"), req.getParameter("sign"));
                    resp.setStatus(201);
                    resp.setContentType("application/json");
                    resp.setCharacterEncoding("UTF-8");
                    resp.getWriter().write(result);
                }
            }
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        var allCurrenciesInJsonFormat = currencyService.findAllCurrencies();
        var writer = resp.getWriter();
        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");
        writer.write(allCurrenciesInJsonFormat);
        writer.close();
    }
}
