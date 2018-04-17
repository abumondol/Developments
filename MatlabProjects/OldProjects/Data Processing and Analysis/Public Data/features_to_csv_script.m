f = realdisp_features;
f = f(f(:, end)==1, 1:end-1);
csvwrite('realdisp_features_1.csv', f);