data = {g};
ttl = '';
sphere_flag = true;
ico_number = 2;
cell_no_flag = true;
scatter_flag = true;
plot_data_sphere_multi_chunks(data, ttl, sphere_flag, ico, ico_number, cell_no_flag, scatter_flag) 

data = {train};
plot_data_sphere_multi_chunks(data, ttl, sphere_flag, ico, ico_number, cell_no_flag, scatter_flag) 
return
%%%%%%%%%%%%%%%%%%%%%%%%%%%%
mu1 = [1 2];
Sigma1 = [2 0; 0 0.5];
mu2 = [-3 -5];
Sigma2 = [1 0;0 1];
rng(1); % For reproducibility
X = [mvnrnd(mu1,Sigma1,1000);mvnrnd(mu2,Sigma2,1000)];

%GMModel = fitgmdist(X,2);
[muhat,sigmahat] = normfit(X)


%%%%%%%%%%%%%%%
d = opp_data(2).session(3).position(1).data(:,10:13);
d = d(500:510, :)/1000;
%d = sum(d.*d, 2)
cos_alpha_by_2 = sqrt(d(:,1).*d(:,1));
sin_alpha_by_2 = sqrt(sum(d(:,2:4).*d(:,2:4), 2));
res = acos(cos_alpha_by_2) - asin(sin_alpha_by_2)
