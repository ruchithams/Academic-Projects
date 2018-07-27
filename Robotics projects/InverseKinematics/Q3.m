% TODO: You write this function!
% input: f1 -> an 9-joint robot encoded as a SerialLink class for one
%              finger
%        f2 -> an 9-joint robot encoded as a SerialLink class for one
%              finger
%        qInit -> 1x11 vector denoting current joint configuration.
%                 First six joints are the arm joints. Joints 8,9 are
%                 finger joints for f1. Joints 10,11 are finger joints
%                 for f2.
%        f1Target, f2Target -> 3x1 vectors denoting the target positions
%                              each of the two fingers.
% output: q -> 1x11 vector of joint angles that cause the fingers to
%              reach the desired positions simultaneously.
%              (orientation is to be ignored)
function q = Q3(f1,f2,qInit,f1Target,f2Target)
  q = qInit;
  for j = 1:300
      
      %fetching joint angles for first finger
      q1= q(1,1:9);
      x = f1.fkine(q1);
      %fetching joint angles for second finger
      q2 = [q(1,1:7) q(1,10:11)];
      z = f2.fkine(q2);
      A = transl(x);
      B = transl(z);
      
      %condition to end loop when goal position is reached
      if A(1) == f1Target(1) && A(2) == f1Target(2) && A(3) == f1Target(3)...
         && B(1) == f2Target(1) && B(2) == f2Target(2) && B(3) == f2Target(3)
         break;
      end
      deltaX1 = (0.05) * (f1Target' - A);
      deltaX2 = (0.05) * (f2Target' - B);
      
      % calculating jacobian 
      J1 = jacob0(f1,q1);
      J2 = jacob0(f2,q2);
      J1 = J1(1 : 3, :);
      J2 = J2(1 : 3, :);
      
      % calulating delta Q using jacobian
      deltaQ1 = pinv(J1) * deltaX1';
      deltaQ2 = pinv(J2) * deltaX2';
      deltaQ1 = deltaQ1';
      deltaQ2 = deltaQ2';
      
      deltaQ = [deltaQ1(1,1:9) 0 0];
      
      %caluculating new joint angles
      q = q + deltaQ;
      q = q + [0 0 0 0 0 0 0 0 0  deltaQ2(1,8:9)];
      
  end


end

   

