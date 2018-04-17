%function [D, d, w] = DTW(t,r)
function distance = DTW(t,r)
% t = t';
% r = r';
% [rows, N]=size(t);
% [rows, M]=size(r);
[N, cols] = size(t);
[M, cols] = size(r);

d = zeros(N, M);
D = zeros(N, M);

for n=1:N
   for m=1:M
       d(n,m)= euclidean_distance(t(n,:),r(m,:));
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


distance= D(N,M);

% n = N;
% m = M;
% k = 1;
% w = [];
% w(1,:)=[N,M];
% while ((n+m)~=2)
%     if (n-1)==0 % n==1
%         m=m-1;
%     elseif (m-1)==0   % m==1
%         n=n-1;
%     else 
%       [values, number] = min([D(n-1,m), D(n,m-1), D(n-1,m-1)]);
%       switch number
%       case 1
%         n=n-1;
%       case 2
%         m=m-1;
%       case 3
%         n=n-1;
%         m=m-1;
%       end
%     end    
%     w = vertcat(w,[n,m]);
% end
% 
% w = flipud(w);

end