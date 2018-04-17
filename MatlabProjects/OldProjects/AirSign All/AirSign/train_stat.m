function train_stat(models)

% for i=1:length(signatures)
%     fprintf('%s\n',signatures(i).file_name);
% end
    
%clc;

count = [models.train_count];
mu = [models.mu];
std = [models.sigma];

train_size = unique([models.train_size]);
sigma_th = unique([models.sigma_th]);
weighted = unique([models.weighted]);
avg_count = mean(count,2);
max_count = max(count);
min_count = min(count);
avg_mu = mean(mu,2);
avg_std = mean(std,2);
avg_norm_std = mean(std./mu,2);

fprintf('%d %.2f, %d, %.2f, %d, %d, %.2f, %.2f, %.5f\n',train_size, sigma_th, weighted, avg_count, min_count, max_count,avg_mu, avg_std, avg_norm_std);
fprintf('Subject, Count, mu, std\n');
for i=1:length(count)
    fprintf('%d %d, %.2f, %.2f\n',i, count(i), mu(i), std(i));
end


%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
end%%%% end of function
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%

