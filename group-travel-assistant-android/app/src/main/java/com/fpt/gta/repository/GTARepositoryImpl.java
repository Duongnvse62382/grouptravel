package com.fpt.gta.repository;

public class GTARepositoryImpl implements GTARepository {
//    @Override
//    public void login(final Context context, String username, String password, final CallBackData<User> callBackData) {
//        ClientApi clientApi = new ClientApi();//call clienApi
//        JSONObject userInfo = new JSONObject();
//        try {
//            userInfo.put("username", username);
//            userInfo.put("password", password);
//        } catch (JSONException e) {
//            e.printStackTrace();
//        }
//        RequestBody body = RequestBody.create(okhttp3.MediaType.parse("application/json; charset=utf-8"), userInfo.toString());
//        Call<ResponseBody> serviceCall = clientApi.passioService().login(body);
//        final KProgressHUD khub = KProgressHUDManager.showProgressBar(context);
//        serviceCall.enqueue(new Callback<ResponseBody>() {
//            @Override
//            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
//                KProgressHUDManager.dismiss(context, khub);
//                if (response.code() == 200 && response.body() != null) {
//                    try {
//                        //get value from body response
//                        String result = response.body().string();
//                        //get type of customer
//                        Type type = new TypeToken<ResponseResult<User>>() {
//                        }.getType();
//                        //call response result to get data
//                        ResponseResult<User> responseResult =
//                                new Gson().fromJson(result, type);
//                        //fill data into customer
//                        if (responseResult.getData() == null) {
//                            callBackData.onFail(response.message());
//                        } else {
//                            User user = responseResult.getData();
//                            callBackData.onSuccess(user);
//                        }
//                    } catch (IOException e) {
//                        e.printStackTrace();
//                    }
//                } else {
//                    callBackData.onFail(response.message());
//                }
//            }
//
//            @Override
//            public void onFailure(Call<ResponseBody> call, Throwable t) {
//                //  KProgressHUDManager.dismiss(context, khub);
//                callBackData.onFail(t.getMessage());
//            }
//        });
//
//    }
//
//    @Override
//    public void getListProduct(final Context context, final CallBackData<List<CategoryInfo>> callBackData) {
//        //initial clientApi
//        ClientApi clientApi = new ClientApi();
//        //initial List Product service and all method getProduct
//        Call<ResponseBody> serviceCall = clientApi.passioService().getListProduct();
//        final KProgressHUD khub = KProgressHUDManager.showProgressBar(context);
//        serviceCall.enqueue(new Callback<ResponseBody>() {
//            @Override
//            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
//                KProgressHUDManager.dismiss(context, khub);
//                if (response != null && response.body() != null) {
//                    if (response.code() == 200) {
//                        try {
//                            //get response result
//                            String result = response.body().string();
//                            Type type = new TypeToken<ResponseResult<ProductAndCategory>>() {
//                            }.getType();
//                            ResponseResult<ProductAndCategory> responseResult = new Gson().fromJson(result, type);
//                            if (responseResult.getData() == null) {
//                                callBackData.onFail(response.message());
//                            }
//                            ProductAndCategory productAndCategoryList = responseResult.getData();
//                            List<CategoryInfo> categoryInfoList = new ArrayList<>();
//
//                            for (Category category : productAndCategoryList.getCategoryList()) {
//                                List<Drink> drinkList = new ArrayList<>();
//                                for (Drink drink : productAndCategoryList.getProductList()) {
//                                    if (drink.getCatId() == category.getCateId()) {
//                                        drinkList.add((Drink) drink.clone());
//
//                                    }
//                                }
//                                if (drinkList.size() > 0) {
//                                    CategoryInfo categoryInfo = new CategoryInfo();
//                                    categoryInfo.setCategory(category);
//                                    for (Drink drink : drinkList) {
//                                        if (drink.getChildProducts() != null && !drink.getChildProducts().isEmpty()) {
//                                            Size size = drink.getChildProducts().get(0);
//                                            if (size != null) { //if first child is not null
//                                                if (size.getPicUrl() != null && !drink.getPicUrl().isEmpty()) {
//                                                    drink.setPicUrl(size.getPicUrl());
//                                                    drink.setDescription(size.getDescription());
//                                                }
//                                            }
//                                        }
//                                    }
//                                    categoryInfo.setDrinkList(drinkList);
//                                    categoryInfoList.add(categoryInfo);
//                                }
//                            }
//                            callBackData.onSuccess(categoryInfoList);
//                        } catch (IOException e) {
//                            e.printStackTrace();
//                        } catch (CloneNotSupportedException e) {
//                            e.printStackTrace();
//                        }
//                    } else {
//                        //if response is fail => return message fail
//                        callBackData.onFail(response.message());
//                    }
//                }
//            }
//
//            @Override
//            public void onFailure(Call<ResponseBody> call, Throwable t) {
//                //if response is fail => return message fail
//                KProgressHUDManager.dismiss(context, khub);
//                callBackData.onFail(t.getMessage());
//            }
//        });
//    }
}
