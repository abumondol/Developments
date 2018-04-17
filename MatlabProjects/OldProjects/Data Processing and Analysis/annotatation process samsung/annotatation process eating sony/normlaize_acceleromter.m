a = csvread('subject1_right_acl.csv');
b = a(:, 2:4);
b = smooth(b, 0.8);
mag = sqrt(sum(b.*b,2));
b = b./[mag mag mag];
a(:,2:4) = b;
csvwrite('subject1_right_acl_normalized.csv', a);