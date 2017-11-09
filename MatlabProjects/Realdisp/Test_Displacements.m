pos = 3;
k = 1;
res = [];
gmm1 = GMModels{10};

for s = 2
    for c=4:7
        accel = data(s).mutual(c).pos(3).accel;
        mag = sum(accel.*accel, 2);
        mag = abs(mag-95.25);
        count = length(mag);
        for i=1:500:count-500
            if max(mag(i:i+499))<50
                d = accel(i:i+499, :);
                gmm2 = fitgmdist(d, 1);
                min_kld = -1;
                for k=1:10                    
                    mu1 = gmm1.mu(k, :);
                    mu2 = gmm2.mu;
                    sigma1 = gmm1.Sigma(:,:,k);
                    sigma2 = gmm2.Sigma;
                    
                    kld = KL_divergence(mu1, mu2, sigma1, sigma2);
                    if min_kld <0 || kld<min_kld
                        min_kld = kld;                    
                    end
                end
                
                res = [res; s, c, kld];
                
            end
        end
        
    end
end

r = res(res(:,2) ==4, 3);
figure
hist(r, 100)

r = res(res(:,2) ==5, 3);
figure
hist(r, 100)

r = res(res(:,2) ==6, 3);
figure
hist(r, 100)

r = res(res(:,2) ==7, 3);
figure
hist(r, 100)

