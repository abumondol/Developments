%f = opp_features;
%f = f(f(:, end)==1 & f(:,end-1)<=5, 1:end-2);
csvwrite('opp_features_right.csv', f);