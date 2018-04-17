function dist = DTW(t,r)
    N=length(t);
    M=length(r);
    d = zeros(N, M);
    D = zeros(N, M);

    for n=1:N
       for m=1:M
           %d(n,m)= euclidean_distance(t(:,n),r(:,m));
           d(n,m)= 1 - sum(t(n,:).*r(m,:), 2);
       end
    end

    D(1,1)=d(1,1);

    for n=2:N
        D(n,1)=d(n,1)+D(n-1,1);
    end

    for m=2:M
        D(1,m)=d(1,m)+D(1,m-1);
    end

    for n=2:N
        for m=2:M
            D(n,m)=d(n,m) + min([D(n-1,m), D(n,m-1), D(n-1,m-1)]);
        end
    end

    i = 16;
    j = 16;
    dist = [];
    while i<=N && j<=M
        dist = [dist, D(i,j)];
        i = i + 8;
        j = j + 8;
    end
    %dist = D(N,M);

end