package org.example;

import org.springframework.stereotype.Service;

@Service
public class SellerCore {

    public int[] sellerCore( int cost, int discount, int price) {
        int[] temp = {0, 0, 0};
        float new_discount = 0f;
        float cost_f = cost;
        if (price < 50) {
            // System.out.println("Товару указана цена меньше 50 руб.");
            temp[0] = cost;
            temp[1] = discount;
            temp[2] = 4;
            return temp;
        }
        new_discount = 100 - (price * 100f / cost);
        int n_discount = Math.round(new_discount);
        float n_discount_f = (float) n_discount;

        if (n_discount < 3 | n_discount > 95) {
            if (n_discount < 3) {
                float new_cost = price / (1 - 3f / 100);
                int new_cost_f = (int) new_cost;
                float delta = new_cost % 1;
                if (delta > 0.001) new_cost_f = new_cost_f + 1;
                int d_d = (int) (cost_f * 19 / 100);
                int d_d_2 = cost + d_d;
                if (new_cost_f <= d_d_2) {
                    temp[0] = new_cost_f;
                    temp[1] = 3;
                    temp[2] = 0;
                    return temp;
                } else {
                  /*  System.out.println("Цена товара не изменена. " +
                            "Заявленная цена не соответствует правилам сайта. " +
                            "Укажите ниже цену.");*/
                    temp[0] = cost;
                    temp[1] = discount;
                    temp[2] = 3;
                    return temp;
                }
            }
            if (n_discount > 95) {
                float new_cost = price / (1 - 95f / 100);
                int new_cost_f = (int) new_cost;
                float delta = new_cost % 1;
                if (delta > 0.001) new_cost_f = new_cost_f + 1;
                int d_d = (int) (cost_f * 19 / 100);
                int d_d_2 = cost - d_d;
                if (new_cost_f >= d_d_2) {
                    temp[0] = new_cost_f;
                    temp[1] = 95;
                    temp[2] = 0;
                    return temp;
                } else {
                    temp[0] = new_cost_f;
                    temp[1] = 95;
                    temp[2] = 2;
                    return temp;
                }
            }
            return null;
        } else if (n_discount > 70 & discount == 0) {
            float new_cost = price / (1 - 70f / 100);
            int new_cost_f = (int) new_cost;
            float delta = new_cost % 1;
            if (delta > 0.001) new_cost_f = new_cost_f + 1;
            int d_d = (int) (cost_f * 20 / 100);
            int d_d_2 = cost - d_d;
            if (new_cost_f >= d_d_2) {
                temp[0] = new_cost_f;
                temp[1] = 70;
                temp[2] = 0;
                return temp;
            } else {
               /* System.out.println("Скорее всего данный товар новый на сайте. " +
                        "Скидка не может быть выше 60% по правилам маркетплейса. " +
                        "Цена товара до скидки была снижена более 20% ");*/
                temp[0] = new_cost_f;
                temp[1] = 70;
                temp[2] = 1;
                return temp;
            }
        } else {
            float new_cost = price / (1 - n_discount_f / 100);
            int new_cost_f = (int) new_cost;
            float delta = new_cost % 1;
            if (delta > 0.001) new_cost_f = new_cost_f + 1;
            temp[0] = new_cost_f;
            temp[1] = n_discount;
            return temp;
        }
    }

}
