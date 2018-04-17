close all;clc;
names = {'Abu','Asif','Ayesha','Chong','Emi','Jisa','Liza','Masud','Meiyi','Nahid','Nazia','Nowshin','Ridwan','Tonmoy','Wasi','Yan','Zeya'};
fprintf('Total subjects: %d\n',length(names));
names(15)
false_names = {'Abu','Emi','Liza','Wasi'};
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
read_raw_data_false(names, false_names);
return
load('signatures');
transformation(names, signatures);
return;

sigma_th = 3;
fprintf('Test_airsign\n');
fprintf('sigma: 0, 1, 2, 3, 4, 5  \n');
for train_size = 5:5
    fprintf('AirSign Train size: %d, Weighted: False\n', train_size);
    models = analyze_train_mauth(names, signatures, train_size, false, sigma_th);
    analyze_test_mauth(names, signatures, train_size, false, sigma_th, models);
end

return
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%

result = read_raw_data(names);
if result==false
    return;
end

result = process_data();
if result==false
    return;
end

load('signatures');

%sign_list = [255 251 673 537 656 253 245 534 596 697];
%draw_signatures(signatures(sign_list), 1); % 1 for mark points
%draw_signatures(signatures(1:10), 2);  % 2 for peaks
%draw_signatures(signatures, 3);  % 3 for raw signature after cutting stationary parts at start and end

train_size = 5;
fprintf('Train_mauth\nsigma_th, weighted, avg_count, min_count, max_count,avg_mu, avg_std, avg_norm_std\n');
for sigma_th = 3:-0.5:1
    analyze_train_mauth(names, signatures, train_size, false, sigma_th);
    analyze_train_mauth(names, signatures, train_size, true, sigma_th);
end


%draw_dtw_mappings();

%length_analysis(names, signatures);
%airsign_analysis(signatures, mauth_grid, train_size);
%mauth_analysis(signatures, mauth_grid, train_size);









