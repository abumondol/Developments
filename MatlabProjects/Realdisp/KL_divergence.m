function res = KL_divergence(mu1, mu2, sigma1, sigma2)
    mu1 = transpose(mu1);
    mu2 = transpose(mu2);
        
    a = trace(inv(sigma2)*sigma1);
    b = transpose(mu2-mu1)*inv(sigma2)*(mu2-mu1);
    c = log(det(sigma2)/det(sigma1));
    
    res = (a + b - 3 + c)/2;

end