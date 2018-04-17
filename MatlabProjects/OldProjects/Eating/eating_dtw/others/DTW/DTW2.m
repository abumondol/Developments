function d = DTW2(t,r)
    N=length(t);
    d = 0;
    for i=1:N    
       d = d + 1 - sum(t(i,:).*r(i,:), 2);       
    end
end